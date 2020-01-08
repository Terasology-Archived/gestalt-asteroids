#version 330

in vec3 Position;
in vec2 TexCoord;
out vec2 TexCoord0;

void main(){
    gl_Position = vec4(Position.x * 0.5, Position.y * 0.5, Position.z, 1.0);
    TexCoord0 = vec2(TexCoord.x, TexCoord.y);
}