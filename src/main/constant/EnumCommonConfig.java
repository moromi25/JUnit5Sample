package main.constant;

public enum EnumCommonConfig {
    UNDEFINED(-1),// ���ʐݒ肪���ݒ�
    UNUSE(0),// �S�Јꗥ�ŗ��p���Ȃ�
    USE(1),// �S�Јꗥ�ŗ��p����
    DEPEND_ON_EMPLOYEE(2);// �Ј��ɐݒ���ς˂�

    private final int val;

    private EnumCommonConfig(int val) {
        this.val = val;
    }

    public int getVal() {
        return val;
    }
}
