package pl.tuso.essentials.entity;

import net.minecraft.core.Rotations;

public interface ArmorStandProperties {
    void setInvisible(boolean invisible);

    boolean isBaby();

    void setSmall(boolean small);

    boolean isSmall();

    void setShowArms(boolean showArms);

    boolean isShowArms();

    void setNoBasePlate(boolean hideBasePlate);

    boolean isNoBasePlate();

    void setMarker(boolean marker);

    boolean isMarker();

    void setHeadPose(Rotations angle);

    void setBodyPose(Rotations angle);

    void setLeftArmPose(Rotations angle);

    void setRightArmPose(Rotations angle);

    void setLeftLegPose(Rotations angle);

    void setRightLegPose(Rotations angle);

    Rotations getHeadPose();

    Rotations getBodyPose();

    Rotations getLeftArmPose();

    Rotations getRightArmPose();

    Rotations getLeftLegPose();

    Rotations getRightLegPose();
}
