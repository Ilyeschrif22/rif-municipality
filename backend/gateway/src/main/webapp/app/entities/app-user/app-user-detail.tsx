import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './app-user.reducer';

export const AppUserDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const appUserEntity = useAppSelector(state => state.gateway.appUser.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="appUserDetailsHeading">
          <Translate contentKey="gatewayApp.appUser.detail.title">AppUser</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{appUserEntity.id}</dd>
          <dt>
            <span id="login">
              <Translate contentKey="gatewayApp.appUser.login">Login</Translate>
            </span>
          </dt>
          <dd>{appUserEntity.login}</dd>
          <dt>
            <span id="firstName">
              <Translate contentKey="gatewayApp.appUser.firstName">First Name</Translate>
            </span>
          </dt>
          <dd>{appUserEntity.firstName}</dd>
          <dt>
            <span id="lastName">
              <Translate contentKey="gatewayApp.appUser.lastName">Last Name</Translate>
            </span>
          </dt>
          <dd>{appUserEntity.lastName}</dd>
          <dt>
            <span id="email">
              <Translate contentKey="gatewayApp.appUser.email">Email</Translate>
            </span>
          </dt>
          <dd>{appUserEntity.email}</dd>
          <dt>
            <span id="phone">
              <Translate contentKey="gatewayApp.appUser.phone">Phone</Translate>
            </span>
          </dt>
          <dd>{appUserEntity.phone}</dd>
          <dt>
            <span id="role">
              <Translate contentKey="gatewayApp.appUser.role">Role</Translate>
            </span>
          </dt>
          <dd>{appUserEntity.role}</dd>
          <dt>
            <span id="cin">
              <Translate contentKey="gatewayApp.appUser.cin">Cin</Translate>
            </span>
          </dt>
          <dd>{appUserEntity.cin}</dd>
          <dt>
            <span id="address">
              <Translate contentKey="gatewayApp.appUser.address">Address</Translate>
            </span>
          </dt>
          <dd>{appUserEntity.address}</dd>
          <dt>
            <span id="birthDate">
              <Translate contentKey="gatewayApp.appUser.birthDate">Birth Date</Translate>
            </span>
          </dt>
          <dd>
            {appUserEntity.birthDate ? <TextFormat value={appUserEntity.birthDate} type="date" format={APP_LOCAL_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="municipalityId">
              <Translate contentKey="gatewayApp.appUser.municipalityId">Municipality Id</Translate>
            </span>
          </dt>
          <dd>{appUserEntity.municipalityId}</dd>
        </dl>
        <Button tag={Link} to="/app-user" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/app-user/${appUserEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default AppUserDetail;
