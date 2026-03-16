# -*- coding: utf-8 -*-

from odoo import models, fields, api

class EtiquetaProducto(models.Model):
    _name = 'loop_proyecto.etiqueta_producto'
    _description = 'Etiqueta del producto'
    _order = 'name'

    name = fields.Char(string='Nombre', required=True, index=True)
    active = fields.Boolean(string='Activa', default=True)
    
    _sql_constraints = [
        ('name_uniq', 'unique(name)', 'Ya existe una etiqueta con ese nombre.'),
    ]