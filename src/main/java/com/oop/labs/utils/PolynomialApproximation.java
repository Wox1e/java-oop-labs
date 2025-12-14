package com.oop.labs.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс для полиномиальной аппроксимации точек функции
 * Использует Apache Commons Math для построения полинома
 */
public class PolynomialApproximation {
    private static final Logger logger = LogManager.getLogger(PolynomialApproximation.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    // Максимальная степень полинома для аппроксимации
    private static final int MAX_DEGREE = 10;
    // Минимальная степень полинома
    private static final int MIN_DEGREE = 1;

    /**
     * Аппроксимирует точки полиномом и возвращает коэффициенты
     * @param points список точек (x, y)
     * @return массив коэффициентов полинома [a0, a1, a2, ..., an] для полинома a0 + a1*x + a2*x^2 + ... + an*x^n
     */
    public static double[] approximate(List<Point> points) {
        if (points == null || points.size() < 2) {
            throw new IllegalArgumentException("Недостаточно точек для аппроксимации (минимум 2)");
        }

        int n = points.size();
        // Выбираем степень полинома
        int degree = Math.min(Math.max(MIN_DEGREE, n / 2), MAX_DEGREE);
        
        logger.debug("Аппроксимируем {} точек полиномом степени {}", n, degree);

        // Используем Apache Commons Math для аппроксимации
        WeightedObservedPoints obs = new WeightedObservedPoints();
        for (Point point : points) {
            obs.add(point.x, point.y);
        }

        PolynomialCurveFitter fitter = PolynomialCurveFitter.create(degree);
        double[] coefficients = fitter.fit(obs.toList());
        
        return coefficients;
    }

    /**
     * Восстанавливает точки из полинома в заданном диапазоне
     * @param coefficients коэффициенты полинома
     * @param xMin минимальное значение x
     * @param xMax максимальное значение x
     * @param pointCount количество точек для восстановления
     * @return список восстановленных точек
     */
    public static List<Point> restorePoints(double[] coefficients, double xMin, double xMax, int pointCount) {
        List<Point> points = new ArrayList<>();
        
        if (coefficients == null || coefficients.length == 0) {
            return points;
        }

        double step = (xMax - xMin) / (pointCount - 1);
        
        for (int i = 0; i < pointCount; i++) {
            double x = xMin + i * step;
            double y = evaluatePolynomial(coefficients, x);
            points.add(new Point(x, y));
        }
        
        return points;
    }

    /**
     * Вычисляет значение полинома в точке x
     * @param coefficients коэффициенты полинома [a0, a1, a2, ..., an]
     * @param x значение аргумента
     * @return значение полинома: a0 + a1*x + a2*x^2 + ... + an*x^n
     */
    public static double evaluatePolynomial(double[] coefficients, double x) {
        if (coefficients == null || coefficients.length == 0) {
            return 0.0;
        }

        double result = 0.0;
        double xPower = 1.0;
        
        for (double coefficient : coefficients) {
            result += coefficient * xPower;
            xPower *= x;
        }
        
        return result;
    }

    /**
     * Сериализует коэффициенты в JSON строку
     */
    public static String coefficientsToJson(double[] coefficients) throws JsonProcessingException {
        if (coefficients == null) {
            return "[]";
        }
        return objectMapper.writeValueAsString(coefficients);
    }

    /**
     * Десериализует коэффициенты из JSON строки
     */
    public static double[] jsonToCoefficients(String json) throws JsonProcessingException {
        if (json == null || json.trim().isEmpty() || json.trim().equals("[]")) {
            return new double[0];
        }
        return objectMapper.readValue(json, double[].class);
    }

    /**
     * Внутренний класс для представления точки
     */
    public static class Point {
        public final double x;
        public final double y;

        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }
}

