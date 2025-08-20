import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Municipality from './municipality';
import MunicipalityDetail from './municipality-detail';
import MunicipalityUpdate from './municipality-update';
import MunicipalityDeleteDialog from './municipality-delete-dialog';

const MunicipalityRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Municipality />} />
    <Route path="new" element={<MunicipalityUpdate />} />
    <Route path=":id">
      <Route index element={<MunicipalityDetail />} />
      <Route path="edit" element={<MunicipalityUpdate />} />
      <Route path="delete" element={<MunicipalityDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default MunicipalityRoutes;
