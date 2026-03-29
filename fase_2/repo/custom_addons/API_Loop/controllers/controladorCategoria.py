# -*- coding: utf-8 -*-
from odoo import http
from odoo.http import request
from .controladorToken import get_current_user_from_token
import json

class controladorCategoria(http.Controller):

    # --------------------------------------------------------------------------
    #  LISTAR TODAS LAS CATEGORÍAS (GET)
    # --------------------------------------------------------------------------
    @http.route('/api/v1/loop/categorias', auth='none', methods=['GET'], csrf=False, type='http')
    def list_categorias(self):

        user = get_current_user_from_token()

        if not user:
            return request.make_response(
                json.dumps({'error': 'Unauthorized'}),
                headers=[('Content-Type', 'application/json')],
                status=401
            )

        categorias = request.env['loop_proyecto.categoria'].sudo().search([])

        result = []

        for categoria in categorias:
            result.append({
                'id': categoria.id,
                'nombre': categoria.nombre,
            })

        return request.make_response(
            json.dumps({'success': True, 'categorias': result}),
            headers=[('Content-Type', 'application/json')],
            status=200
        )
