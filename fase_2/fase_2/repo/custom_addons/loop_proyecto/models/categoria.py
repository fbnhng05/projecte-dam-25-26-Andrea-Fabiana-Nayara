# -*- coding: utf-8 -*-

from odoo import models, fields

class categoria(models.Model):
    _name='loop_proyecto.categoria'
    _description='Modelo de categoría'
    _rec_name = 'nombre'

    nombre = fields.Char(
        string='Nombre de la categoría',
        required=True,
        index=True,
        store=True,
        help='Nombre descriptivo de la categoría'
    )

    descripcion = fields.Text(
        string='Descripción',
        required=True,
        store=True,
        help='Descripción detallada de la categoría'
    )

    imagen = fields.Binary(
        string='Imagen de la categoría',
        required=True,
        store=True,
        help='Imagen representativa de la categoría'
    )