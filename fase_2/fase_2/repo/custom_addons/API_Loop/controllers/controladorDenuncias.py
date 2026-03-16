# -*- coding: utf-8 -*-

import json # json 
from odoo import http # type: ignore
from odoo.http import request # type: ignore
from .controladorToken import get_current_user_from_token

class CRUD_Denuncias_Controller(http.Controller):

    """
    ENDPOINT: REGISTRAR DENUNCIA

    La denuncia puede ser bien un producto o un
    """

    @http.route('/api/v1/loop/denuncias', type='json', auth='none', csrf=False, cors='*', methods=['POST'])
    def api_denuncias(self, **params):

        user = get_current_user_from_token()
        if not user:
            return {'error': 'Unauthorized'}

        data = params.get('data')

        if not data:
            return {'error': 'No se han enviado datos'}
        
        required = ['motivo_denuncia']

        if 'motivo_denuncia' not in data:
            return {'error': 'Falta el campo motivo_denuncia'}
            
        producto_id = data.get('producto_id')
        comentario_id = data.get('comentario_id')

        if not producto_id and not comentario_id:
            return {"error": "Debes indicar un producto o un comentario a denunciar"}
        
        usuario_denunciado_id = None
        if producto_id:
            producto = request.env['loop_proyecto.producto'].sudo().browse(producto_id)
            if not producto.exists(): 
                return {'error': 'Producto no encontrado'}
            usuario_denunciado_id = producto.propietario_id.id
        elif comentario_id:
            comentario = request.env['loop_proyecto.comentario'].sudo().browse(comentario_id)
            if not comentario.exists():
                return {'error': 'Comentario no encontrado'}
            usuario_denunciado_id = comentario.comentador_id.id

        try:
            denuncia = request.env['loop_proyecto.denuncia_reporte'].sudo().create({
                'denunciante_id': user.id,
                'motivo_denuncia': data['motivo_denuncia'],
                'producto_id': producto_id,
                'comentario_id': comentario_id,
                'usuario_denunciado_id': usuario_denunciado_id
            })
            return {
                'success': 'OK',
                'id': denuncia.id
            }
        except Exception as e:
            return {'error': str(e)}
        
    """
    ENDPOINT: OBTENER DENUNCIAS

    Para los endpoints de type json hay que poner un body en el json, aunque sea para un GET y esté vacío. Cuerpo vacío:

    {
        "jsonrpc": "2.0",
        "method": "call",
        "params": {}
    }

    """

    @http.route('/api/v1/loop/denuncias', type='json', auth='none', cors='*', csrf=False, methods=['GET'])
    def api_get_denuncias(self, **params):
        
        user = get_current_user_from_token()

        if not user:
            return {'error': 'Unauthorized'}
        
        denuncias_user = request.env['loop_proyecto.denuncia_reporte'].sudo().search([
            ('denunciante_id', '=', user.id)
        ])

        if not denuncias_user:
            return {'empty': 'Este usuario no tiene denuncias'}
        
        else:
            return {
                'denuncias': [
                    {
                        'id': d.id,
                        'motivo_denuncia': d.motivo_denuncia,
                        'producto_id': d.producto_id.id if d.producto_id else None,
                        'comentario_id': d.comentario_id.id if d.comentario_id else None,
                        'usuario_denunciado_id': d.usuario_denunciado_id.id
                    }
                    for d in denuncias_user
                ]
            }
        
    """
    ENDPOINT: ACLARAR DENUNCIA (MODIFICAR)
        La única modificacion que un usuario debería poder hacer es retirar su denuncia.
        Para ello sólo se modifica un campo.
    """
    
    @http.route('/api/v1/loop/denuncias', type='json', auth='none', cors='*', csrf=False, methods=['PATCH'])
    def api_patch_denuncia(self, **params):

        user = get_current_user_from_token()

        if not user:
            return {'error': 'Unauthorized'}
        else:
            data = params.get('data')
            if not data:
                return {'error': 'No data update'}
            else:
                denuncia_id = data.get('denuncia_id')
                if not denuncia_id:
                    return{'error': 'Falta el campo denuncia_id'}
                
                denuncia = request.env['loop_proyecto.denuncia_reporte'].sudo().browse(denuncia_id)
                if not denuncia.exists():
                    return{'error': 'Denuncia no encontrada'}
                
                denuncia.write({'estado_usuario': 'retirada'})
                return {'success': 'OK'}

