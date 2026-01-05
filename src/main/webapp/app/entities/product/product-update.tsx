import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getOrders } from 'app/entities/order/order.reducer';
import { createEntity, getEntity, reset, updateEntity } from './product.reducer';

export const ProductUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const orders = useAppSelector(state => state.order.entities);
  const productEntity = useAppSelector(state => state.product.entity);
  const loading = useAppSelector(state => state.product.loading);
  const updating = useAppSelector(state => state.product.updating);
  const updateSuccess = useAppSelector(state => state.product.updateSuccess);

  const handleClose = () => {
    navigate(`/product${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getOrders({}));
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
    if (values.price !== undefined && typeof values.price !== 'number') {
      values.price = Number(values.price);
    }
    if (values.qty !== undefined && typeof values.qty !== 'number') {
      values.qty = Number(values.qty);
    }

    const entity = {
      ...productEntity,
      ...values,
      orders: mapIdList(values.orders),
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
          ...productEntity,
          orders: productEntity?.orders?.map(e => e.id.toString()),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="jhipsterMvcApp.product.home.createOrEditLabel" data-cy="ProductCreateUpdateHeading">
            Create or edit a Product
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? <ValidatedField name="id" required readOnly id="product-id" label="ID" validate={{ required: true }} /> : null}
              <ValidatedField label="Name" id="product-name" name="name" data-cy="name" type="text" />
              <ValidatedField label="Price" id="product-price" name="price" data-cy="price" type="text" />
              <ValidatedField label="Qty" id="product-qty" name="qty" data-cy="qty" type="text" />
              <ValidatedField label="Orders" id="product-orders" data-cy="orders" type="select" multiple name="orders">
                <option value="" key="0" />
                {orders
                  ? orders.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/product" replace color="info">
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

export default ProductUpdate;
