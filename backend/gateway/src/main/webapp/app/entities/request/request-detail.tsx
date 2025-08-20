import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './request.reducer';

export const RequestDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const requestEntity = useAppSelector(state => state.gateway.request.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="requestDetailsHeading">
          <Translate contentKey="gatewayApp.request.detail.title">Request</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{requestEntity.id}</dd>
          <dt>
            <span id="type">
              <Translate contentKey="gatewayApp.request.type">Type</Translate>
            </span>
          </dt>
          <dd>{requestEntity.type}</dd>
          <dt>
            <span id="description">
              <Translate contentKey="gatewayApp.request.description">Description</Translate>
            </span>
          </dt>
          <dd>{requestEntity.description}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="gatewayApp.request.status">Status</Translate>
            </span>
          </dt>
          <dd>{requestEntity.status}</dd>
          <dt>
            <span id="createdDate">
              <Translate contentKey="gatewayApp.request.createdDate">Created Date</Translate>
            </span>
          </dt>
          <dd>
            {requestEntity.createdDate ? <TextFormat value={requestEntity.createdDate} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="resolvedDate">
              <Translate contentKey="gatewayApp.request.resolvedDate">Resolved Date</Translate>
            </span>
          </dt>
          <dd>
            {requestEntity.resolvedDate ? <TextFormat value={requestEntity.resolvedDate} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="citizenId">
              <Translate contentKey="gatewayApp.request.citizenId">Citizen Id</Translate>
            </span>
          </dt>
          <dd>{requestEntity.citizenId}</dd>
          <dt>
            <span id="municipalityId">
              <Translate contentKey="gatewayApp.request.municipalityId">Municipality Id</Translate>
            </span>
          </dt>
          <dd>{requestEntity.municipalityId}</dd>
        </dl>
        <Button tag={Link} to="/request" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/request/${requestEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default RequestDetail;
