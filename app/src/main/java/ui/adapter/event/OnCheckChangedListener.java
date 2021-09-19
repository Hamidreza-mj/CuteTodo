package ui.adapter.event;

import model.Todo;

public interface OnCheckChangedListener {
    void onChange(Todo todo);
}
