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
uniform vec2 u_playerPosition;
uniform float u_time;

void main() {
    gl_FragColor = texture2D(u_texture, v_texCoords);
    vec2 pos = v_texCoords * u_screenSize;
    float r1 = 0.3 * u_time * u_time;
    float r2 = 0.1 * u_time * u_time + 3.5 * u_time - 40.0;
    float r3 = r2 - 100.0;
    bool invert = distance(pos, u_playerPosition) < min(1.5, (60.0 - u_time) * 0.1);
    invert = invert ^^ (distance(pos, u_playerPosition) < r1);
    invert = invert ^^ (distance(pos, vec2(u_playerPosition.x - 30.0, u_playerPosition.y)) < r2);
    invert = invert ^^ (distance(pos, vec2(u_playerPosition.x + 30.0, u_playerPosition.y)) < r2);
    invert = invert ^^ (distance(pos, vec2(u_playerPosition.x, u_playerPosition.y - 30.0)) < r2);
    invert = invert ^^ (distance(pos, vec2(u_playerPosition.x, u_playerPosition.y + 30.0)) < r2);
    invert = invert ^^ (distance(pos, vec2(u_playerPosition.x, u_playerPosition.y)) < r3);
    if (invert) {
        gl_FragColor.rgb = 1 - gl_FragColor.rgb;
    }
}