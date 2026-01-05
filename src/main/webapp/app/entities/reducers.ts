import product from 'app/entities/product/product.reducer';
import order from 'app/entities/order/order.reducer';
import orderProduct from 'app/entities/order-product/order-product.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const entitiesReducers = {
  product,
  order,
  orderProduct,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
};

export default entitiesReducers;
