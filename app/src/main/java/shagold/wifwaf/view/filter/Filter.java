package shagold.wifwaf.view.filter;

public interface Filter<U, V> {

    V meetFilter(U text);

}
