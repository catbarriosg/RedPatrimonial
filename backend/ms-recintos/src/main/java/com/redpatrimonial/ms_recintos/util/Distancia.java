package com.redpatrimonial.ms_recintos.util;

// Distancia en línea recta entre dos coordenadas GPS (fórmula de Haversine)
public final class Distancia {

    private static final double RADIO_TIERRA_KM = 6371.0;

    private Distancia() {
    }

    public static double kilometros(double lat1, double lng1, double lat2, double lng2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        return RADIO_TIERRA_KM * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}
