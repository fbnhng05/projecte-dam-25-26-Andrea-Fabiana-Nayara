
# -*- coding: utf-8 -*-
import jwt
import datetime
import logging
_logger = logging.getLogger(__name__)

from odoo import http
from odoo.http import request

# Función para obtener una clave secreta

def _get_secret_key():
    # Mejor que hardcodear: config param del sistema
    return request.env['ir.config_parameter'].sudo().get_param('jwt.secret_key') or ',loop461.047S@_*#abf.WFW.' # Obtiene clave secreta del archivo config.parameter o sino 'una_clave_secreta_Segura'

# Devuelve un token con Bearer

def _get_bearer_token():
    auth_header = request.httprequest.headers.get('Authorization', '')
    if not auth_header.startswith('Bearer '):
        return None
    return auth_header.split(' ', 1)[1].strip()

# Obtiene un usuario a partir del token generado 

def get_current_user_from_token():
    token = _get_bearer_token()
    if not token:
        _logger.error("NO SE HA OBTENIDO EL TOKEN")
        return None
    
    # Esto es lo importante:
    # El token da un cacho grande de letras y números

    try:
        payload = jwt.decode(token, _get_secret_key(), algorithms=['HS256']) # decodifica usando el token, la clave secreta y el algortimo, obteniendo nombre usuario, password, y fecha y hora de inicio de sesión
        uid = payload.get('uid') # Del payload obtiene la uid
        if not uid:
            _logger.error("NO SE HA OBTENIDO EL UID")
            return None
        user = request.env['res.partner'].sudo().browse(uid) # Busca si existe
        return user if user.exists() else None
    except jwt.ExpiredSignatureError: # No devuelve nada si el token ha caducado
        _logger.warning("EXPIRADO")
        return None
    except jwt.InvalidTokenError: # No devuelve nada si no se ha encontrado nada
        _logger.warning("INVÁLIDO")
        return None


class JWTAuthController(http.Controller):

    @http.route('/api/v1/loop/auth', type='json', auth='none', csrf=False, cors='*', methods=['POST'])
    def authenticate(self, **kw): 

        params = kw.get("params",kw)
        
        username = (params.get("username") or "").strip()
        password = params.get("password") or ""

        """
            Hay que enviar los datos a la API con esta estructura:

            "jsonrpc": "2.0",
            "method": "call",
            "params": {
                "username": "antonio",
                "password": "123456"
                }
            }

        """

        if not username or not password:
            _logger.warning("MISSING username/password")
            return {"ok": False, 'error': 'Missing username/password'}

        user = request.env['res.partner'].sudo().search([('username','=',username), ('password','=',password)], limit=1)

        if not user:
            _logger.warning("INVALID CREDENTIALS")
            return {'error': 'Invalid credentials'}
        
        now = datetime.datetime.now()
        payload = {
            'uid': user.id,
            'username': user.username,
            'exp': now + datetime.timedelta(hours=1),
            'iat': now
        }

        token = jwt.encode(payload, _get_secret_key(), algorithm='HS256')
        if isinstance(token, bytes):
            token = token.decode('utf-8')

        # Se debe trabajar siempre con el token para cualquier operación, esto nos evita problema de hackeos
        return {'token': token}
    
    # Obtiene los datos del usuario

    @http.route('/api/v1/loop/me/data', type='json', auth='none', csrf=False, cors='*', methods=['POST'])
    def get_user_data(self, **kw):
        user = get_current_user_from_token()
        if not user:
            _logger.error("NO SE HA OBTENIDO EL USUARIO")
            return {'error': 'Unauthorized'}

        return {
            'username': user.username
        }