import appUser from 'app/entities/app-user/app-user.reducer';
import municipality from 'app/entities/municipality/municipality.reducer';
import request from 'app/entities/request/request.reducer';
import document from 'app/entities/document/document.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const entitiesReducers = {
  appUser,
  municipality,
  request,
  document,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
};

export default entitiesReducers;
