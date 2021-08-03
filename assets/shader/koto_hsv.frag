#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

varying LOWP vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;

const float eps = 1.0e-10;

vec4 rgb2hsv(vec4 c) {
    vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);
    vec4 p = mix(vec4(c.bg, K.wz), vec4(c.gb, K.xy), step(c.b, c.g));
    vec4 q = mix(vec4(p.xyw, c.r), vec4(c.r, p.yzx), step(p.x, c.r));

    float d = q.x - min(q.w, q.y);
    return vec4(abs(q.z + (q.w - q.y) / (6.0 * d + eps)), d / (q.x + eps), q.x, c.a);
}

vec4 hsv2rgb(vec4 c) {
    const vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return vec4(c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y), c.a);
}

void main() {
    gl_FragColor = rgb2hsv(texture2D(u_texture, v_texCoords));
    gl_FragColor.x += v_color.x;
    gl_FragColor.yzw *= v_color.yzw;
    gl_FragColor = hsv2rgb(gl_FragColor);
}