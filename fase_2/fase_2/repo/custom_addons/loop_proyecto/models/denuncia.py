# -*- coding: utf-8 -*-
 
from odoo import models, fields, api
from odoo.exceptions import ValidationError


class DenunciaReporte(models.Model):
    _name = 'loop_proyecto.denuncia_reporte'
    _description = 'Denuncia de comentarios o productos'
    _order = 'create_date desc'

    denunciante_id = fields.Many2one(
        'res.partner',
        string='Usuario denunciante',
        required=True,
        default=lambda self: self.env.user.partner_id,
        index=True
    )

    producto_id = fields.Many2one(
        'loop_proyecto.producto',
        string='Producto denunciado',
        index=True
    )

    comentario_id = fields.Many2one(
        'loop_proyecto.comentario',
        string='Comentario denunciado',
        index=True
    )

    comentario_texto = fields.Text(
        string='Contenido del comentario',
        related='comentario_id.contenido',
        readonly=True,
        store=True
    )

    usuario_denunciado_id = fields.Many2one(
        'res.partner',
        string='Usuario denunciado',
        index=True,
        readonly=True
    )

    fecha_emision = fields.Datetime(
        string='Fecha de emisión',
        default=fields.Datetime.now,
        readonly=True
    )

    motivo_denuncia = fields.Text(
        string='Motivo de la denuncia',
        required=True
    )

    estado_moderacion = fields.Selection(
        [
            ('pendiente', 'Pendiente'),
            ('revisada', 'Revisada'),
            ('cerrada', 'Cerrada'),
        ],
        string='Estado Moderación',
        default='pendiente',
        index=True
    )

    estado_usuario = fields.Selection(
        [
            ('activa', 'Activa'),
            ('retirada', 'Retirada'),
        ],
        string='Estado Usuario',
        default='activa',
        index=True
    )

    resultado_moderacion = fields.Selection(
        [
            ('valida', 'Denuncia válida'),
            ('no_valida', 'Denuncia no válida'),
            ('duplicada', 'Duplicada'),
        ],
        string='Resultado moderación'
    )

    empleado_id = fields.Many2one(
        'res.users',
        string='Empleado revisor'
    )

    fecha_revision = fields.Datetime(
        string='Fecha de revisión'
    )

    @api.constrains('estado_usuario')
    def _check_estado_usuario(self):
        for record in self:
            if (
                record.estado_usuario == 'activa'
                and record._origin.estado_usuario == 'retirada'
            ):
                raise ValidationError(
                    "No se puede reactivar una denuncia retirada."
                )
