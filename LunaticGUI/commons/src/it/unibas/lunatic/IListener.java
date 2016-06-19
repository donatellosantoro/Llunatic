package it.unibas.lunatic;

public interface IListener<Bean> {

    void remove();

    void onChange(IModel model, Bean bean);
}
