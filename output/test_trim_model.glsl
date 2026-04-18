case 8: {
    modelSize /= 14;
    float t = ANIMATION_TIME(1.0);
    const float[3] rotTimes = float[3](0.0, 0.5, 1.0);
    const vec3[3] rotValues = vec3[3](vec3(0.0, -45.0, 0.0), vec3(0.0, 45.0, 0.0), vec3(0.0, -45.0, 0.0));
    const int[3] rotInterpolationTypes = int[3](1, 1, 1);
    vec3 bone1Pos = vec3(0, 0, 4);
    vec3 bone1Scale = vec3(8, 8, 8);
    vec3 rot = interpolate(rotTimes, rotValues, rotInterpolationTypes, t);
    mat3 bone1Rot = Rotate3(rot.x * 0.017453292, 0) * Rotate3(rot.y * 0.017453292, 1) * Rotate3(rot.z * 0.017453292, 2);
    vec3 bone1Pivot = vec3(0, 0, -4);
    ADD_BOX_ROTATE(bone1Pos, bone1Scale, bone1Rot, bone1Pivot, vec4(stp + vec2(-40, -16), vec2(8, -8)), vec4(stp + vec2(-24, -32), vec2(8, -8)), vec4(stp + vec2(-40, -40), vec2(8, 8)), vec4(stp + vec2(-40, -32), vec2(8, 8)), vec4(stp + vec2(-32, -40), vec2(8, 8)), vec4(stp + vec2(-32, -32), vec2(8, 8)));
    break;
}