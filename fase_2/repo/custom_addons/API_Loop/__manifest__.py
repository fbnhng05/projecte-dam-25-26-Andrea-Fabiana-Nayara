# -*- coding: utf-8 -*-
{
    'name': "Autenticación con Token JWT",

    'summary': "Short (1 phrase/line) summary of the module's purpose",

    'description': """
Long description of module's purpose
    """,

    'author': "Andrea, Fabiana y Nayara",
    'website': "https://www.yourcompany.com",

    # Categories can be used to filter modules in modules listing
    # Check https://github.com/odoo/odoo/blob/15.0/odoo/addons/base/data/ir_module_category_data.xml
    # for the full list
    'category': 'Technical',
    'version': '0.1',

    # any module necessary for this one to work correctly
    'depends': [
        'base',
        'product',        # 👈 CLAVE
        'loop_proyecto',
    ],

    'external_dependencies': {
        'python': ['jwt']
    },

    # always loaded
    'data': [ ],
    'installable': True,
    'application': False,

}

