#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

varying LOWP vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform vec2 u_screenSize;
uniform vec2 u_bossPosition;
uniform float u_radius;
uniform float u_time;

void main() {
    vec2 pos = v_texCoords * u_screenSize - u_bossPosition;
    float theta = atan(pos.y, pos.x);
    float border = u_radius * (1.0 +
    0.06 * sin(5.0 * theta - 0.08 * u_time) +
    0.01 * sin(7.0 * theta + 0.04 * u_time) +
    0.02 * sin(8.0 * theta - 0.06 * u_time) +
    0.03 * sin(11.0 * theta + 0.1 * u_time));
    float l = length(pos);
    pos *= 1.0 + (smoothstep(0.2 * u_radius, u_radius, l) - smoothstep(u_radius, 1.5 * u_radius, l)) * (border - u_radius) / l;
    float f = 1.0 - smoothstep(border - 20.0, border + 30.0, length(pos));
    pos.x += f * sin(pos.y / 20.0 + u_time * 3.141592653 / 30.0) * 8.0;
    pos.y += f * sin(pos.x / 20.0 + u_time * 2.718281828 / 120.0) * 3.0;
    gl_FragColor = texture2D(u_texture, (pos + u_bossPosition) / u_screenSize);
}