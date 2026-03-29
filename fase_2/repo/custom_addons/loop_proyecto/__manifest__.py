# -*- coding: utf-8 -*-
{
    'name': "Loop",
    'summary': "App para compra-venta de productos",
    'description': """
        Con Loop puedes comprar y vender productos de manera fácil y rápida.
        Plataforma de compra-venta para el proyecto DAM.
    """,

    'author': "Andrea, Fabiana y Nayara",
    'website': "https://www.yourcompany.com",

    # CATEGORÍA VÁLIDA
    'category': 'Productivity',
    'version': '1.0',

    # MUY IMPORTANTE
    'installable': True,
    'application': True,
    'icon': '/base/static/description/icon.png',

    # any module necessary for this one to work correctly
    'depends': ['base', 'hr'],

    'data': [
        'security/ir.model.access.csv',
        #'data/mail_template_usuarios.xml',
        'views/report_producto.xml',
        'views/vista_producto.xml',
        'views/vista_categoria.xml',
        'views/vista_comentario.xml',
        'views/vista_denuncia.xml',
        'views/vista_empleado.xml',
        'views/vista_etiquetas.xml',
        'views/vista_usuario_app.xml',
    ],
}
