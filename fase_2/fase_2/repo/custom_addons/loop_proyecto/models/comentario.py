# -*- coding: utf-8 -*-
from odoo import api, fields, models, _

class Comentario(models.Model):
    _name = 'loop_proyecto.comentario'
    _description = 'Comentario de productos'

    contenido = fields.Text(string='Contenido del comentario', required=True)
    fecha_creacion = fields.Datetime(string='Fecha de creación', default=fields.Datetime.now, readonly=True)
    
    comentador_id = fields.Many2one(
        'res.partner',
        string='Usuario autor',
        required=True,
        readonly=True
    )
    
    producto_id = fields.Many2one(
        'loop_proyecto.producto',
        string='Producto',
        required=True,
        index=True,
        ondelete='cascade'
    )
    
    estado = fields.Selection(
        [('published', 'Publicado'), ('hidden', 'Oculto'), ('deleted', 'Eliminado')],
        string='Estado',
        default='published',
        index=True
    )
    
    moderador_id = fields.Many2one('res.partner', string='Moderado por')
    fecha_moderacion = fields.Datetime(string='Fecha moderación')
