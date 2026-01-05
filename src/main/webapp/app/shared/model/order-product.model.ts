import { IOrder } from 'app/shared/model/order.model';
import { IProduct } from 'app/shared/model/product.model';

export interface IOrderProduct {
  id?: number;
  orderQty?: number | null;
  order?: IOrder | null;
  product?: IProduct | null;
}

export const defaultValue: Readonly<IOrderProduct> = {};
