package kr.moonshine.glsl;

public enum GlslVersion {
    V150("150"),
    V330("330"),
    ;

    private final String versionString;

    GlslVersion(String versionString) {
        this.versionString = versionString;
    }

    public String versionString() {
        return versionString;
    }
}
