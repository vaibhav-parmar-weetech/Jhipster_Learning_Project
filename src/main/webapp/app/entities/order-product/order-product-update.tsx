import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getOrders } from 'app/entities/order/order.reducer';
import { getEntities as getProducts } from 'app/entities/product/product.reducer';
import { createEntity, getEntity, reset, updateEntity } from './order-product.reducer';

export const OrderProductUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const orders = useAppSelector(state => state.order.entities);
  const products = useAppSelector(state => state.product.entities);
  const orderProductEntity = useAppSelector(state => state.orderProduct.entity);
  const loading = useAppSelector(state => state.orderProduct.loading);
  const updating = useAppSelector(state => state.orderProduct.updating);
  const updateSuccess = useAppSelector(state => state.orderProduct.updateSuccess);

  const handleClose = () => {
    navigate('/order-product');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getOrders({}));
    dispatch(getProducts({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    if (values.id !== undefined && typeof values.id !== 'number') {
      values.id = Number(values.id);
    }
    if (values.orderQty !== undefined && typeof values.orderQty !== 'number') {
      values.orderQty = Number(values.orderQty);
    }

    const entity = {
      ...orderProductEntity,
      ...values,
      order: orders.find(it => it.id.toString() === values.order?.toString()),
      product: products.find(it => it.id.toString() === values.product?.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          ...orderProductEntity,
          order: orderProductEntity?.order?.id,
          product: orderProductEntity?.product?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="jhipsterMvcApp.orderProduct.home.createOrEditLabel" data-cy="OrderProductCreateUpdateHeading">
            Create or edit a Order Product
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField name="id" required readOnly id="order-product-id" label="ID" validate={{ required: true }} />
              ) : null}
              <ValidatedField label="Order Qty" id="order-product-orderQty" name="orderQty" data-cy="orderQty" type="text" />
              <ValidatedField id="order-product-order" name="order" data-cy="order" label="Order" type="select">
                <option value="" key="0" />
                {orders
                  ? orders.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField id="order-product-product" name="product" data-cy="product" label="Product" type="select">
                <option value="" key="0" />
                {products
                  ? products.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/order-product" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">Back</span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp; Save
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default OrderProductUpdate;
