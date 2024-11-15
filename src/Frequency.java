public record Frequency(double frequencyInFrames, double loudness) {
    double getIntensity(long frame) {
        return Math.sin(frame / this.frequencyInFrames * 2 * Math.PI) * this.loudness;
    }
}
