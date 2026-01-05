import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import OrderProduct from './order-product';
import OrderProductDetail from './order-product-detail';
import OrderProductUpdate from './order-product-update';
import OrderProductDeleteDialog from './order-product-delete-dialog';

const OrderProductRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<OrderProduct />} />
    <Route path="new" element={<OrderProductUpdate />} />
    <Route path=":id">
      <Route index element={<OrderProductDetail />} />
      <Route path="edit" element={<OrderProductUpdate />} />
      <Route path="delete" element={<OrderProductDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default OrderProductRoutes;
