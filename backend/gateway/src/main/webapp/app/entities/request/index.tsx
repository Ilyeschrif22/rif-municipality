import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Request from './request';
import RequestDetail from './request-detail';
import RequestUpdate from './request-update';
import RequestDeleteDialog from './request-delete-dialog';

const RequestRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Request />} />
    <Route path="new" element={<RequestUpdate />} />
    <Route path=":id">
      <Route index element={<RequestDetail />} />
      <Route path="edit" element={<RequestUpdate />} />
      <Route path="delete" element={<RequestDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default RequestRoutes;
