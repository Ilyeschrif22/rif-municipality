export interface IMunicipality {
  id?: number;
  name?: string;
  region?: string;
  country?: string;
}

export const defaultValue: Readonly<IMunicipality> = {};
