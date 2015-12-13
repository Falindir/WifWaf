package shagold.wifwaf.view.filter;

/**
 * Created by jimmy on 29/11/15.
 */
public interface Filter<U, V> {

    V meetFilter(U text);

}
