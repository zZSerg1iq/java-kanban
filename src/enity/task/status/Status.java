package enity.task.status;

public enum Status {
    NEW("������ ������ �������, �� � � ���������� ��� �� ����������"),
    IN_PROGRESS("��� ������� ������ ������"),
    DONE("������ ���������");

    private String description;

    Status(String description) {
        this.description = description;
    }
}
