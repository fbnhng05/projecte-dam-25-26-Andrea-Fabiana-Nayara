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
        
        required = ['partner_id', 'contenido']

        for field in required:
            if field not in data:
                return {'error': f'Falta el campo {field}'}

        try:
            comentario = request.env['loop_proyecto.comentario'].sudo().create({
                'partner_id': data['partner_id'],
                'comentador_id': user.id,
                'contenido': data['contenido'],
                'estado': data.get('estado', 'published'),
            })

            valoracion_value = data.get('valoracion')
            if valoracion_value is not None and 0 <= valoracion_value <= 5:
                existing = request.env['loop_proyecto.valoracion'].sudo().search([
                    ('usuario_comentador', '=', user.id),
                    ('usuario_valorado', '=', data['partner_id'])
                ], limit=1)
                if existing:
                    existing.write({'valoracion': valoracion_value})
                else:
                    request.env['loop_proyecto.valoracion'].sudo().create({
                        'usuario_comentador': user.id,
                        'usuario_valorado': data['partner_id'],
                        'valoracion': valoracion_value,
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
        
        if 'contenido' not in data:
            return {'error': 'Falta el campo contenido'}

        try:
            comentario.write({
                'contenido': data['contenido'],
                'estado': data.get('estado', comentario.estado),
                'moderador_id': data.get('moderador_id', None),
                'fecha_moderacion': data.get('fecha_moderacion', None),
            })

            valoracion_value = data.get('valoracion')
            if valoracion_value is not None and 0 <= valoracion_value <= 5:
                existing = request.env['loop_proyecto.valoracion'].sudo().search([
                    ('usuario_comentador', '=', comentario.comentador_id.id),
                    ('usuario_valorado', '=', comentario.partner_id.id)
                ], limit=1)
                if existing:
                    existing.write({'valoracion': valoracion_value})
                else:
                    request.env['loop_proyecto.valoracion'].sudo().create({
                        'usuario_comentador': comentario.comentador_id.id,
                        'usuario_valorado': comentario.partner_id.id,
                        'valoracion': valoracion_value,
                    })

            return {'success': True, 'comentario_id': comentario.id}
        except Exception as e:
            return {'error': str(e)}
        
    # --------------------------------------------------------------------------
    #  OBTENER COMENTARIOS DE UN PRODUCTO (GET)
    # --------------------------------------------------------------------------
    @http.route('/api/v1/loop/usuarios/<int:partner_id>/comentarios', auth='none', methods=['GET'], csrf=False, type='json')
    def get_comentarios_usuario(self, partner_id):

        user = get_current_user_from_token()

        if not user:
            return {'error': 'Unauthorized'}

        comentarios = request.env['loop_proyecto.comentario'].sudo().search([
            ('partner_id', '=', partner_id),
            ('estado', '=', 'published')
        ])

        resultado = []
        for c in comentarios:
            valoracion_record = request.env['loop_proyecto.valoracion'].sudo().search([
                ('usuario_comentador', '=', c.comentador_id.id),
                ('usuario_valorado', '=', partner_id)
            ], limit=1)
            valoracion_val = valoracion_record.valoracion if valoracion_record else None

            resultado.append({
                'id': c.id,
                'contenido': c.contenido,
                'fecha_creacion': str(c.fecha_creacion),
                'comentador': c.comentador_id.name,
                'comentador_partner_id': c.comentador_id.id,
                'imagen_comentador': c.comentador_id.image_1920.decode('utf-8') if c.comentador_id.image_1920 else None,
                'valoracion': valoracion_val,
                'estado': c.estado,
                'moderador': c.moderador_id.name if c.moderador_id else None,
                'fecha_moderacion': str(c.fecha_moderacion) if c.fecha_moderacion else None,
            })
        return {'comentarios': resultado}

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