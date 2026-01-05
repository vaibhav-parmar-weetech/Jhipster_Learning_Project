import { IOrder } from 'app/shared/model/order.model';

export interface IProduct {
  id?: number;
  name?: string | null;
  price?: number | null;
  qty?: number | null;
  orders?: IOrder[] | null;
}

export const defaultValue: Readonly<IProduct> = {};
