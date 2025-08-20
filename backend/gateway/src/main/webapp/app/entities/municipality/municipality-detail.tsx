import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './municipality.reducer';

export const MunicipalityDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const municipalityEntity = useAppSelector(state => state.gateway.municipality.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="municipalityDetailsHeading">
          <Translate contentKey="gatewayApp.municipality.detail.title">Municipality</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{municipalityEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="gatewayApp.municipality.name">Name</Translate>
            </span>
          </dt>
          <dd>{municipalityEntity.name}</dd>
          <dt>
            <span id="region">
              <Translate contentKey="gatewayApp.municipality.region">Region</Translate>
            </span>
          </dt>
          <dd>{municipalityEntity.region}</dd>
          <dt>
            <span id="country">
              <Translate contentKey="gatewayApp.municipality.country">Country</Translate>
            </span>
          </dt>
          <dd>{municipalityEntity.country}</dd>
        </dl>
        <Button tag={Link} to="/municipality" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/municipality/${municipalityEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default MunicipalityDetail;
