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
    bool invert = distance(pos, u_playerPosition) < 1.5;
    invert = invert ^^ (distance(pos, u_playerPosition) < u_time * 10.0);
    invert = invert ^^ (distance(pos, vec2(u_playerPosition.x - 30.0, u_playerPosition.y)) < (u_time - 8.0) * 8.0);
    invert = invert ^^ (distance(pos, vec2(u_playerPosition.x + 30.0, u_playerPosition.y)) < (u_time - 8.0) * 8.0);
    invert = invert ^^ (distance(pos, vec2(u_playerPosition.x, u_playerPosition.y - 30.0)) < (u_time - 8.0) * 8.0);
    invert = invert ^^ (distance(pos, vec2(u_playerPosition.x, u_playerPosition.y + 30.0)) < (u_time - 8.0) * 8.0);
    invert = invert ^^ (distance(pos, vec2(u_playerPosition.x, u_playerPosition.y)) < (u_time - 30.0) * 8.0);
    if (invert) {
        gl_FragColor.rgb = 1 - gl_FragColor.rgb;
    }
}