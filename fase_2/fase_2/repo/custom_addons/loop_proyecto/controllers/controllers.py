# -*- coding: utf-8 -*-
# from odoo import http


# class LoopProyecto(http.Controller):
#     @http.route('/loop_proyecto/loop_proyecto', auth='public')
#     def index(self, **kw):
#         return "Hello, world"

#     @http.route('/loop_proyecto/loop_proyecto/objects', auth='public')
#     def list(self, **kw):
#         return http.request.render('loop_proyecto.listing', {
#             'root': '/loop_proyecto/loop_proyecto',
#             'objects': http.request.env['loop_proyecto.loop_proyecto'].search([]),
#         })

#     @http.route('/loop_proyecto/loop_proyecto/objects/<model("loop_proyecto.loop_proyecto"):obj>', auth='public')
#     def object(self, obj, **kw):
#         return http.request.render('loop_proyecto.object', {
#             'object': obj
#         })

