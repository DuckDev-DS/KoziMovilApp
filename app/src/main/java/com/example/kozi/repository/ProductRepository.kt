package com.example.kozi.repository

import com.example.kozi.R
import com.example.kozi.model.Category
import com.example.kozi.model.Product

object ProductRepository {
    val categories = listOf(
        Category(1, "Pulseras"),
        Category(2, "Collares"),
        Category(3, "Anillos")
    )

    fun getProducts(): List<Product> {
        return listOf(
            // Pulseras
            Product(1, "Pulsera vintage", "Pulsera vintage con piedra negra y anillo conectado", 8900.0, categories[0], R.drawable.pulcera1),
            Product(2, "Pulsera de piedras", "Pulsera de piedras negras con cruces plateadas", 8500.0, categories[0], R.drawable.pulcera2),
            Product(3, "Pulsera de malla", "Pulsera de malla negra estilo guante", 12500.0, categories[0], R.drawable.pulcera3),
            Product(4, "Pulsera de púas", "Pulsera de púas plateadas", 7500.0, categories[0], R.drawable.pulcera4),
            Product(5, "Pulsera plateada", "Pulsera plateada con dije de corazón gótico y cruz", 9500.0, categories[0], R.drawable.pulcera5),
            Product(6, "Pulsera plateada", "Pulsera plateada con dijes de cruz y daga", 9200.0, categories[0], R.drawable.pulcera6),
            Product(7, "Pulsera ancha", "Pulsera ancha de piedras negras con calaveras plateadas y corazón rojo", 14500.0, categories[0], R.drawable.pulcera7),
            Product(8, "Pulsera de encaje", "Pulsera de encaje con rosa roja y anillo conectado", 9800.0, categories[0], R.drawable.pulcera8),

            // Collares
            Product(9, "Choker negro", "Choker negro con colgante de corazón", 6800.0, categories[1], R.drawable.collar1),
            Product(10, "Choker de cuero", "Choker de cuero negro con argolla metálica", 6200.0, categories[1], R.drawable.collar2),
            Product(11, "Choker negro", "Choker negro murciélago con piedra rojas", 8500.0, categories[1], R.drawable.collar3),
            Product(12, "Collar de piedras", "Collar de piedras negras con colgante de cuervo", 9500.0, categories[1], R.drawable.collar4),
            Product(13, "Pack de 2 collares", "Pack de 2 collares de cruces plateadas", 13500.0, categories[1], R.drawable.collar5),
            Product(14, "Choker de encaje", "Choker de encaje con rosa roja", 7800.0, categories[1], R.drawable.collar6),
            Product(15, "Collar de cadena", "Collar de cadena con corazón rojo gótico", 8800.0, categories[1], R.drawable.collar7),
            Product(16, "Choker negro", "Choker negro con colgante de corazón", 7200.0, categories[1], R.drawable.collar8),
            Product(17, "Choker negro", "Choker negro con luna creciente negra", 6500.0, categories[1], R.drawable.collar9),
            Product(18, "Collar con cruz", "Collar con cruz gótica", 9200.0, categories[1], R.drawable.collar10),
            Product(19, "Collar de piedras", "Collar de piedras negras con colgante de cuervo", 10500.0, categories[1], R.drawable.collar11),
            Product(20, "Collar con cruz", "Collar con cruz ornamentada", 9800.0, categories[1], R.drawable.collar12),

            // Anillos
            Product(21, "Pack de anillos", "Pack de anillos gótico con calavera y murciélago", 20500.0, categories[2], R.drawable.anillo1),
            Product(22, "Anillo plateado", "Anillo plateado con diseño de cuervo", 7500.0, categories[2], R.drawable.anillo2),
            Product(23, "Anillo de cuervo", "Anillo de cuervo con piedra roja", 8500.0, categories[2], R.drawable.anillo3),
            Product(24, "Anillo de plata", "Anillo de plata en forma de tumba", 6500.0, categories[2], R.drawable.anillo4),
            Product(25, "Set de anillos", "Set de anillos góticos con diferentes diseños", 30500.0, categories[2], R.drawable.anillo5),
            Product(26, "Anillo de murciélago", "Anillo de murciélago conectado con pulsera plateada", 11500.0, categories[2], R.drawable.anillo6),
            Product(27, "Anillo con diseño", "Anillo con diseño de murciélago", 7200.0, categories[2], R.drawable.anillo7),
            Product(28, "Anillo gótico", "Anillo gótico con cuervo y piedra roja colgante", 9800.0, categories[2], R.drawable.anillo8),
            Product(29, "Anillo con alas", "Anillo con alas extendidas y gema roja", 9200.0, categories[2], R.drawable.anillo9),
            Product(30, "Anillo de cuervo", "Anillo de cuervo con detalles ornamentales", 8800.0, categories[2], R.drawable.anillo10),
            Product(31, "Anillo de Rosa", "Anillo en forma de rosa negra", 6800.0, categories[2], R.drawable.anillo11),
            Product(32, "Pack de anillos", "Pack de anillos de rosa roja y púrpura", 16500.0, categories[2], R.drawable.anillo12)
        )
    }
}