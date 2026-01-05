import dayjs from 'dayjs';
import { IUser } from 'app/shared/model/user.model';
import { IProduct } from 'app/shared/model/product.model';

export interface IOrder {
  id?: number;
  orderDate?: dayjs.Dayjs | null;
  status?: string | null;
  totalAmount?: number | null;
  user?: IUser | null;
  products?: IProduct[] | null;
}

export const defaultValue: Readonly<IOrder> = {};
