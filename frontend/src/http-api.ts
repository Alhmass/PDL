import axios, { AxiosResponse, AxiosError } from 'axios';
import { ImageType, ImageSimilarType } from '@/image';

const instance = axios.create({
  baseURL: "/",
  timeout: 15000,
});

const responseBody = (response: AxiosResponse) => response.data;

const requests = {
  get: (url: string, param: {}) => instance.get(url, param).then(responseBody),
  post: (url: string, body: {}) => instance.post(url, body, { headers: { "Content-Type": "multipart/form-data" }, }).then(responseBody),
  put: (url: string, body: {}) => instance.put(url, body).then(responseBody),
  delete: (url: string) => instance.delete(url).then(responseBody)
};

export const api = {
  getImageList: (): Promise<ImageType[]> => requests.get('images', {}),
  getImage: (id: number): Promise<Blob> => requests.get(`images/${id}`, { responseType: "blob" }),
  createImage: (form: FormData): Promise<ImageType> => requests.post('images', form),
  deleteImage: (id: number): Promise<void> => requests.delete(`images/${id}`),
  getImageListSimilar: (id: number, descriptor: string, nb: number, tag?: string): Promise<ImageSimilarType[]> => requests.get(`images/${id}/similar`, { params: { number: nb, descriptor: descriptor, tags: tag }, }),
  getImageFilter: (id: number, filter: string, nb?: string): Promise<ImageType[]> => requests.get(`images/${id}/filter`, { params: { filter: filter, number: nb }, }),
  getImageByTags: (tags: string): Promise<ImageType[]> => requests.get(`images/search`, { params: { tags: tags } }),
  getImageTags: (id: number): Promise<string[]> => requests.get(`images/${id}/tags`, {}),
  editTags: (id: number, form: FormData): Promise<void> => requests.post(`images/${id}/edit/tags`, form)
}