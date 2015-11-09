package it.unibas.lunatic;

public interface IModel {

    <BeanType> BeanType get(String key, Class<BeanType> beanClass);

    void put(String s, Object value);

    boolean remove(String s);

    boolean remove(String s, Object o);

    @SuppressWarnings("rawtypes")
    public void notifyChange(String key, Class beanClass);

    String getName();
}
