import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate, byteSize, openFile } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './document.reducer';

export const DocumentDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const documentEntity = useAppSelector(state => state.gateway.document.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="documentDetailsHeading">
          <Translate contentKey="gatewayApp.document.detail.title">Document</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{documentEntity.id}</dd>
          <dt>
            <span id="title">
              <Translate contentKey="gatewayApp.document.title">Title</Translate>
            </span>
          </dt>
          <dd>{documentEntity.title}</dd>
          <dt>
            <span id="type">
              <Translate contentKey="gatewayApp.document.type">Type</Translate>
            </span>
          </dt>
          <dd>{documentEntity.type}</dd>
          <dt>
            <span id="issueDate">
              <Translate contentKey="gatewayApp.document.issueDate">Issue Date</Translate>
            </span>
          </dt>
          <dd>{documentEntity.issueDate ? <TextFormat value={documentEntity.issueDate} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="file">
              <Translate contentKey="gatewayApp.document.file">File</Translate>
            </span>
          </dt>
          <dd>
            {documentEntity.file ? (
              <div>
                {documentEntity.fileContentType ? (
                  <a onClick={openFile(documentEntity.fileContentType, documentEntity.file)}>
                    <Translate contentKey="entity.action.open">Open</Translate>&nbsp;
                  </a>
                ) : null}
                <span>
                  {documentEntity.fileContentType}, {byteSize(documentEntity.file)}
                </span>
              </div>
            ) : null}
          </dd>
          <dt>
            <span id="fileContentType">
              <Translate contentKey="gatewayApp.document.fileContentType">File Content Type</Translate>
            </span>
          </dt>
          <dd>{documentEntity.fileContentType}</dd>
          <dt>
            <span id="citizenId">
              <Translate contentKey="gatewayApp.document.citizenId">Citizen Id</Translate>
            </span>
          </dt>
          <dd>{documentEntity.citizenId}</dd>
        </dl>
        <Button tag={Link} to="/document" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/document/${documentEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default DocumentDetail;
