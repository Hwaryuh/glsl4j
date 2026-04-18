if (max(max(armorMarkerNormal.a, armorMarkerMirroredX.a), armorMarkerMirroredY.a) == 252.0 && (faceID == 15 || faceID == 17)) {
    vec4 armorTestColorIn = vec4(-1);
    if (armorMarkerNormal == vec4(255.0, 0.0, 1.0, 252.0)) {
        armorTestColorIn = armorMarkerNormal;
    } else if (armorMarkerMirroredX == vec4(255.0, 0.0, 1.0, 252.0)) {
        armorTestColorIn = armorMarkerMirroredX;
    } else if (armorMarkerMirroredY == vec4(255.0, 0.0, 1.0, 252.0)) {
        armorTestColorIn = armorMarkerMirroredY;
    }
    if (armorTestColorIn == vec4(255.0, 0.0, 1.0, 252.0)) {
        if (faceID == 15) {
            cem = 8;
            cem_reverse = 1;
        } else {
            cem = 9;
            cem_reverse = 1;
        }
        cem_size = 2.0;
    } else {
        gl_Position = vec4(0);
    }
}