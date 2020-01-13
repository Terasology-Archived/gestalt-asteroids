#version 330

in vec3 Position;
in vec2 TexCoord;

uniform mat4 worldviewperpective;

out vec2 TexCoord0;

void main(){
    gl_Position = worldviewperpective * vec4(Position.x, Position.y, Position.z, 1.0);
    TexCoord0 = vec2(TexCoord.x, TexCoord.y);
}