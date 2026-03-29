# -*- coding: utf-8 -*-

import json
import base64
from odoo import http
from odoo.http import request
from .controladorToken import get_current_user_from_token
from pathlib import Path

def img_a_base64(ruta):
    """ Convierte una imagen de la ruta proporcionada a base64 """
    try:
        if not Path(ruta).exists():
            raise FileNotFoundError(f"La ruta {ruta} no es válida.")
        
        with open(ruta, "rb") as image_file:
            return base64.b64encode(image_file.read()).decode('utf-8')
        
    except Exception as e:
        return None

class CRUD_User_Controller(http.Controller):
 
    """
    ENDPOINT: REGISTRAR USUARIO
    """

    @http.route('/api/v1/loop/register', type='json', auth='none', csrf=False, cors='*', methods=['POST'])
    def api_register(self, **params):

        data = params.get('data') # recoge data con los parametros que se le pasan

        # Ejemplo de cómo tiene que ser la estructura a la hora de enviar los datos por JSON:
        #{
        #    "jsonrpc": "2.0",
        #    "method": "call",
        #    "params": {
        #        "data": {
        #            "name": "Juan Jose",
        #            "username": "Juan",
        #            "password": "juan123",
        #            "email": "juan@test.com"
        #        }
        #    }
        #}

        if not data:
            return {'error': 'No se han enviado datos'}
        
        required = ['name','username','password','email'] # datos obligatorios que se controlará más tarde en la app del usuario

        for field in required:
            if field not in data:
                return {'error': f'Falta el campo {field}'}
            

        # Evitar duplicados, en la app también hay que controlar esto, pero se hace también por si acaso en la app se válido el usuario de alguna manera

        if request.env['res.partner'].sudo().search([('username','=',data['username'])], limit=1):
            return {'error': 'El username ya existe'}

        # Se crea el usuario

        try:
            user = request.env['res.partner'].sudo().create({
                'name': data['name'], 
                'username': data['username'],
                'password': data['password'],
                'email': data.get('email')
            })

            return {'success': True}
        except Exception as e:
            return {'error': str(e)}

    """
    ENDPOINT: OBTENER USUARIO
    """

    @http.route('/api/v1/loop/me', type='json', auth="none", cors='*', csrf=False, methods=["GET"])
    def api_get_user(self, **params):
        
        user = get_current_user_from_token()

        if not user:
            return {'error':'Unauthorized'}

        return {
            'id': user.id,
            'name': user.name,
            'username': user.username,
            'email': user.email,
            'phone': user.phone,
            'mobile': user.mobile,
            'idioma': user.idioma
        }

    """
    ENDPOINT: MODIFICAR USUARIO
    """

    @http.route('/api/v1/loop/me', type='json', auth='none', csrf=False, cors='*', methods=['PATCH'])
    def api_patch_me(self, **params):
        user = get_current_user_from_token()
        if not user:
            return {'error': 'Unauthorized'}

        data = params.get('data')
        if not data:
            return {'error': 'No data to update'}

        allowed = {'name', 'username', 'email', 'phone', 'mobile', 'idioma', 'image_1920'}
        update_vals = {k: v for k, v in data.items() if k in allowed}

        # Validar que image_1920 sea base64 si viene
        if 'image_1920' in update_vals:
            valor = update_vals['image_1920']
            try:
                # quitar espacios y saltos de línea
                valor = valor.replace('\n', '').replace('\r', '')
                base64.b64decode(valor)
            except Exception:
                return {'error': 'La imagen no está en formato base64 válido'}

        if not update_vals:
            return {'error': 'No valid fields to update'}

        request.env['res.partner'].with_user(1).browse(user.id).write(update_vals)
        return {'success': True}


    """
    ENDPOINT: BORRAR USUARIO
    """

    @http.route('/api/v1/loop/me', type='json', auth='none', csrf=False, cors='*', methods=['DELETE'])
    def api_delete_me(self, **params):
        user = get_current_user_from_token()
        if not user:
            return {'error': 'Unauthorized'}

        # Borrar el propio usuario

        request.env['res.partner'].with_user(1).browse(user.id).unlink()

        return {'success': True}