#-*- utf-8 -*-

from odoo import models, api
import random
import string
import logging
_logger = logging.getLogger(__name__)

class Empleado(models.Model):
    _inherit = 'hr.employee'

    @api.model
    def create(self, vals):
        employee = super().create(vals)
        if not employee.user_id:
            login = employee.work_email or employee.name.replace(' ', '.').lower()
            password = "123456"

            group_user = self.env.ref('base.group_user', raise_if_not_found=False)
            if(group_user):
                groups = [(6, 0, [group_user.id])]
            else:
                groups = []

            _logger.info("Creando usuario automático para empleado: %s", employee.name)

            user = self.env['res.users'].create({
                'name': employee.name,
                'login': login,
                'email': employee.id,
                'groups_id': groups,
                'password': password,
                #'force_password_change': True,
            })

            user.action_reset_password()

            #template = self.env.ref('Loop.mail_template_usuario_empleado')
            #if template:
            #    template.sudo().with_context(lang=user.lang).send_email(employee.id, force_send=True)

            return employee