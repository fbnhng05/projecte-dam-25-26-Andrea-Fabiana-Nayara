# -*- coding: utf-8 -*-
from dataclasses import fields
from odoo import http
from odoo.http import request
from pathlib import Path
from .controladorToken import get_current_user_from_token
import json

# CONTROLADOR API REST
# Permite realizar operaciones CRUD sobre comentarios.
# Ejemplos de uso:
# - POST: crear comentario
# - PUT/PATCH: modificar comentario
# - GET: consultar comentario
# - DELETE: eliminar comentario

class controladorComentario(http.Controller):
    
    # --------------------------------------------------------------------------
    #  CREAR COMENTARIO (POST)
    # --------------------------------------------------------------------------
    @http.route('/api/v1/loop/comentarios', auth='none', methods=['POST'], csrf=False, type='json')
    def create_comentario(self, **params):
        
        user = get_current_user_from_token()

        if not user:
            return {'error':'Unauthorized'}
        
        data = params.get('data')
        
        if not data:
            return {'error': 'No se han enviado datos'}
        
        required = ['producto_id','contenido', 'estado']
        
        for field in required:
            if field not in data:
                return {'error': f'Falta el campo {field}'}
            
        try:
            comentario = request.env['loop_proyecto.comentario'].sudo().create({
                'producto_id': data['producto_id'],
                'comentador_id': user.id,
                'contenido': data['contenido'],
                'estado': data['estado'],
            })
            return {'success': True, 'comentario_id': comentario.id}
        except Exception as e:
            return {'error': str(e)}
        
    # --------------------------------------------------------------------------
    #  MODIFICAR COMENTARIO (PATCH)
    # --------------------------------------------------------------------------
    @http.route('/api/v1/loop/comentarios/<int:comentario_id>', auth='none', methods=['PATCH'], csrf=False, type='json')
    def update_comentario(self, comentario_id, **params):
        
        user = get_current_user_from_token()

        if not user:
            return {'error':'Unauthorized'}
        
        comentario = request.env['loop_proyecto.comentario'].sudo().browse(comentario_id)
        if not comentario.exists():
            return {'error': 'Comentario no encontrado'}
        
        data = params.get('data')
        
        if not data:
            return {'error': 'No se han enviado datos'}
        
        required = ['contenido', 'estado']
        
        for field in required:
            if field not in data:
                return {'error': f'Falta el campo {field}'}
            
        try:
            comentario.write({
                'contenido': data['contenido'],
                'estado': data['estado'],
                'moderador_id': data.get('moderador_id', None),
                'fecha_moderacion': data.get('fecha_moderacion', None),
            })
            return {'success': True, 'comentario_id': comentario.id}
        except Exception as e:
            return {'error': str(e)}
        
    # --------------------------------------------------------------------------
    #  CONSULTAR COMENTARIO (GET)
    # --------------------------------------------------------------------------
    @http.route('/api/v1/loop/comentarios/<int:comentario_id>', auth='none', methods=['GET'], csrf=False, type='json')
    def get_comentario(self, comentario_id):
        
        user = get_current_user_from_token()

        if not user:
            return {'error':'Unauthorized'}
        
        comentario = request.env['loop_proyecto.comentario'].sudo().browse(comentario_id)
        if not comentario.exists():
            return {'error': 'Comentario no encontrado'}
            
        try:
            comentario_data = {
                'id': comentario.id,
                'producto_id': comentario.producto_id.id,
                'comentador_id': comentario.comentador_id.id,
                'contenido': comentario.contenido,
                'estado': comentario.estado,
                'fecha_creacion': comentario.fecha_creacion,
                'moderador_id': comentario.moderador_id.id if comentario.moderador_id else None,
                'fecha_moderacion': comentario.fecha_moderacion,
            }
            return {'success': True, 'comentario': comentario_data}
        except Exception as e:
            return {'error': str(e)}

    # --------------------------------------------------------------------------
    #  ELIMINAR COMENTARIO (DELETE)
    # --------------------------------------------------------------------------
    @http.route('/api/v1/loop/comentarios/<int:comentario_id>', auth='none', methods=['DELETE'], csrf=False, type='json')
    def delete_comentario(self, comentario_id):
        
        user = get_current_user_from_token()

        if not user:
            return {'error':'Unauthorized'}
        
        comentario = request.env['loop_proyecto.comentario'].sudo().browse(comentario_id)
        if not comentario.exists():
            return {'error': 'Comentario no encontrado'}
        
        try:
            comentario.unlink()
            return {'success': True}
        except Exception as e:
            return {'error': str(e)}