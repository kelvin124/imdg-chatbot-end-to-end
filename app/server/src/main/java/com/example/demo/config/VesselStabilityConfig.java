package com.example.demo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.vessel.stability")
public class VesselStabilityConfig {

    private Tank tank = new Tank();
    private Kg kg = new Kg();
    private Cg cg = new Cg();
    private Double lcgScalingFactor;

    // Getters and Setters
    public Tank getTank() {
        return tank;
    }

    public void setTank(Tank tank) {
        this.tank = tank;
    }

    public Kg getKg() {
        return kg;
    }

    public void setKg(Kg kg) {
        this.kg = kg;
    }

    public Cg getCg() {
        return cg;
    }

    public void setCg(Cg cg) {
        this.cg = cg;
    }

    public Double getLcgScalingFactor() {
        return lcgScalingFactor;
    }

    public void setLcgScalingFactor(Double lcgScalingFactor) {
        this.lcgScalingFactor = lcgScalingFactor;
    }

    // Nested Classes
    public static class Tank {
        private double fullPercentage;

        public double getFullPercentage() {
            return fullPercentage;
        }

        public void setFullPercentage(double fullPercentage) {
            this.fullPercentage = fullPercentage;
        }
    }

    public static class Kg {
        private double defaultTierHeight;
        private double max;

        public double getDefaultTierHeight() {
            return defaultTierHeight;
        }

        public void setDefaultTierHeight(double defaultTierHeight) {
            this.defaultTierHeight = defaultTierHeight;
        }

        public double getMax() {
            return max;
        }

        public void setMax(double max) {
            this.max = max;
        }
    }

    public static class Cg {
        private double min;
        private double max;

        public double getMin() {
            return min;
        }

        public void setMin(double min) {
            this.min = min;
        }

        public double getMax() {
            return max;
        }

        public void setMax(double max) {
            this.max = max;
        }
    }

}