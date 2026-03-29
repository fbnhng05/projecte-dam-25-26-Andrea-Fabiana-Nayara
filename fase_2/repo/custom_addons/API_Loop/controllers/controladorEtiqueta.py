# -*- coding: utf-8 -*-
from dataclasses import fields
from odoo import http
from odoo.http import request
from pathlib import Path
from .controladorToken import get_current_user_from_token
import json

# CONTROLADOR API REST
# Permite realizar operaciones CRUD sobre las etiquetas.
# Ejemplos de uso:
# - POST: crear etiqueta
# - PUT/PATCH: modificar etiqueta
# - GET: consultar etiqueta
# - DELETE: eliminar etiqueta

class controladorEtiqueta(http.Controller):
    
    # --------------------------------------------------------------------------
    #  CREAR ETIQUETA (POST)
    # --------------------------------------------------------------------------
    @http.route('/api/v1/loop/etiquetas', auth='none', methods=['POST'], csrf=False, type='json')
    def create_etiqueta(self, **params):
        
        user = get_current_user_from_token()

        if not user:
            return {'error':'Unauthorized'}
        
        data = params.get('data')
        
        if not data:
            return {'error': 'No se han enviado datos'}
        
        required = ['name']
        
        for field in required:
            if field not in data:
                return {'error': f'Falta el campo {field}'}
            
        try:
            etiqueta = request.env['loop_proyecto.etiqueta_producto'].sudo().create({
                'name': data['name'],
                'active': data.get('active', True)
            })
            return {'success': True, 'etiqueta_id': etiqueta.id}
        except Exception as e:
            return {'error': str(e)}
        
    # --------------------------------------------------------------------------
    #  MODIFICAR ETIQUETA (PATCH)
    # --------------------------------------------------------------------------
    @http.route('/api/v1/loop/etiquetas/<int:etiqueta_id>', auth='none', methods=['PATCH'], csrf=False, type='json')
    def update_etiqueta(self, etiqueta_id, **params):
        
        user = get_current_user_from_token()

        if not user:
            return {'error':'Unauthorized'}
        
        etiqueta = request.env['loop_proyecto.etiqueta_producto'].sudo().browse(etiqueta_id)
        if not etiqueta.exists():
            return {'error': 'Etiqueta no encontrada'}
        
        data = params.get('data')
        
        if not data:
            return {'error': 'No se han enviado datos'}
        
        required = ['name']
        
        for field in required:
            if field not in data:
                return {'error': f'Falta el campo {field}'}
            
        try:
            etiqueta.write({
                'name': data['name'],
                'active': data.get('active', etiqueta.active)
            })
            return {'success': True, 'name': etiqueta.name, 'active': etiqueta.active}
        except Exception as e:
            return {'error': str(e)}
        
    # --------------------------------------------------------------------------
    #  CONSULTAR ETIQUETA (GET)
    # --------------------------------------------------------------------------
    @http.route('/api/v1/loop/etiquetas/<int:etiqueta_id>', auth='none', methods=['GET'], csrf=False, type='json')
    def get_etiqueta(self, etiqueta_id):
        
        user = get_current_user_from_token()

        if not user:
            return {'error':'Unauthorized'}
        
        etiqueta = request.env['loop_proyecto.etiqueta_producto'].sudo().browse(etiqueta_id)
        if not etiqueta.exists():
            return {'error': 'Etiqueta no encontrada'}
            
        try:
            etiqueta_data = {
                'id': etiqueta.id,
                'name': etiqueta.name,
                'active': etiqueta.active,
            }
            return {'success': True, 'etiqueta': etiqueta_data}
        except Exception as e:
            return {'error': str(e)}

    # --------------------------------------------------------------------------
    #  LISTAR TODAS LAS ETIQUETAS (GET)
    # --------------------------------------------------------------------------
    @http.route('/api/v1/loop/etiquetas', auth='none', methods=['GET'], csrf=False, type='http')
    def list_etiquetas(self):

        user = get_current_user_from_token()

        if not user:
            return request.make_response(
                json.dumps({'error': 'Unauthorized'}),
                headers=[('Content-Type', 'application/json')],
                status=401
            )

        etiquetas = request.env['loop_proyecto.etiqueta_producto'].sudo().search([])

        result = []

        for etiqueta in etiquetas:
            result.append({
                'id': etiqueta.id,
                'name': etiqueta.name,
                'active': etiqueta.active,
            })

        return request.make_response(
            json.dumps({'success': True, 'etiquetas': result}),
            headers=[('Content-Type', 'application/json')],
            status=200
        )

    # --------------------------------------------------------------------------
    #  ELIMINAR ETIQUETA (DELETE)
    # --------------------------------------------------------------------------
    @http.route('/api/v1/loop/etiquetas/<int:etiqueta_id>', auth='none', methods=['DELETE'], csrf=False, type='json')
    def delete_etiqueta(self, etiqueta_id):
        
        user = get_current_user_from_token()

        if not user:
            return {'error':'Unauthorized'}
        
        etiqueta = request.env['loop_proyecto.etiqueta_producto'].sudo().browse(etiqueta_id)
        if not etiqueta.exists():
            return {'error': 'Etiqueta no encontrada'}
        
        try:
            etiqueta.unlink()
            return {'success': True}
        except Exception as e:
            return {'error': str(e)}