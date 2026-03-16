# -*- coding: utf-8 -*-
from odoo import models, fields, api
from odoo.exceptions import ValidationError

class Valoracion(models.Model):
    _name = 'loop_proyecto.valoracion'
    _description = 'Valoración de usuario'

    comentario = fields.Text(
        string='Comentario'
    )
    
    usuario_comentador = fields.Many2one(
        comodel_name='res.partner',
        string='Usuario',
        required=True,
        store=True,
        domain="[('rol', '=', 'cliente')]"
    )

    usuario_valorado = fields.Many2one(
        comodel_name='res.partner',
        string='Usuario valorado',
        required=True,
        ondelete='cascade',
        domain="[('rol', '=', 'cliente')]"
    )

    valoracion = fields.Float(
        string='Valoración',
        required=True,
        default=0,
        help='Valoración de 0 a 5 estrellas.'
    )

    # Restricción: rango de valoración
    @api.constrains('valoracion')
    def _check_valoracion(self):
        for record in self:
            if record.valoracion < 0 or record.valoracion > 5:
                raise ValidationError('La valoración debe estar entre 0 y 5.')

    # Restricción: no auto-valorarse
    @api.constrains('usuario_comentador', 'usuario_valorado')
    def _check_no_self_rating(self):
        for record in self:
            if record.usuario_comentador == record.usuario_valorado:
                raise ValidationError('No puedes valorarte a ti mismo.')

    # Restricción SQL: un usuario solo puede valorar a otro una vez
    _sql_constraints = [
        ('usuario_unique', 'unique(usuario_comentador, usuario_valorado)',
         'Ya has valorado a este usuario.')
    ]
