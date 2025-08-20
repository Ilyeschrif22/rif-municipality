import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { RequestStatus } from 'app/shared/model/enumerations/request-status.model';
import { createEntity, getEntity, reset, updateEntity } from './request.reducer';

export const RequestUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const requestEntity = useAppSelector(state => state.gateway.request.entity);
  const loading = useAppSelector(state => state.gateway.request.loading);
  const updating = useAppSelector(state => state.gateway.request.updating);
  const updateSuccess = useAppSelector(state => state.gateway.request.updateSuccess);
  const requestStatusValues = Object.keys(RequestStatus);

  const handleClose = () => {
    navigate(`/request${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }
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
    values.createdDate = convertDateTimeToServer(values.createdDate);
    values.resolvedDate = convertDateTimeToServer(values.resolvedDate);
    if (values.citizenId !== undefined && typeof values.citizenId !== 'number') {
      values.citizenId = Number(values.citizenId);
    }
    if (values.municipalityId !== undefined && typeof values.municipalityId !== 'number') {
      values.municipalityId = Number(values.municipalityId);
    }

    const entity = {
      ...requestEntity,
      ...values,
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {
          createdDate: displayDefaultDateTime(),
          resolvedDate: displayDefaultDateTime(),
        }
      : {
          status: 'PENDING',
          ...requestEntity,
          createdDate: convertDateTimeFromServer(requestEntity.createdDate),
          resolvedDate: convertDateTimeFromServer(requestEntity.resolvedDate),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="gatewayApp.request.home.createOrEditLabel" data-cy="RequestCreateUpdateHeading">
            <Translate contentKey="gatewayApp.request.home.createOrEditLabel">Create or edit a Request</Translate>
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
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="request-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('gatewayApp.request.type')}
                id="request-type"
                name="type"
                data-cy="type"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('gatewayApp.request.description')}
                id="request-description"
                name="description"
                data-cy="description"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('gatewayApp.request.status')}
                id="request-status"
                name="status"
                data-cy="status"
                type="select"
              >
                {requestStatusValues.map(requestStatus => (
                  <option value={requestStatus} key={requestStatus}>
                    {translate(`gatewayApp.RequestStatus.${requestStatus}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('gatewayApp.request.createdDate')}
                id="request-createdDate"
                name="createdDate"
                data-cy="createdDate"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('gatewayApp.request.resolvedDate')}
                id="request-resolvedDate"
                name="resolvedDate"
                data-cy="resolvedDate"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('gatewayApp.request.citizenId')}
                id="request-citizenId"
                name="citizenId"
                data-cy="citizenId"
                type="text"
              />
              <ValidatedField
                label={translate('gatewayApp.request.municipalityId')}
                id="request-municipalityId"
                name="municipalityId"
                data-cy="municipalityId"
                type="text"
              />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/request" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default RequestUpdate;
