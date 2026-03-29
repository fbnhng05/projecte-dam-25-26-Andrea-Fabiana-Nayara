# -*- coding: utf-8 -*-

from odoo import models, fields, api
from odoo.exceptions import ValidationError

class ImagenProducto(models.Model):
    _name = 'loop_proyecto.producto_imagen'
    _description = 'Imagen del Producto'
    _order = 'sequence, id'

    producto_id = fields.Many2one(
        'loop_proyecto.producto',
        string='Producto',
        required=True,
        ondelete='cascade'
    )

    imagen = fields.Binary(
        string='Imagen',
        required=True
    )

    sequence = fields.Integer(
        string='Orden',
        default=10
    )

    is_principal = fields.Boolean(
        string='Imagen principal'
    )

    """

    @api.constrains('imagen_ids')
    def _comprobar_cantidad_imagenes(self):
        for producto in self:
            count = len(producto.imagen_ids)
            if count < 1 or count > 10:
                raise ValidationError('Un producto debe tener entre 1 y 10 imágenes.')
                """