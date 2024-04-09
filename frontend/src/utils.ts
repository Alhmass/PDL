// routerUtils.ts

import router from '@/router';
import { api } from '@/http-api';
import { ImageType } from '@/image'

export const utils = {
    gotoImage(imageID: number) {
        router.push({ name: 'image', params: { id: imageID } });
    },
    getImages: async (imageList: ImageType[]): Promise<void> => {
        try {
            const data = await api.getImageList();
            imageList.splice(0, imageList.length, ...data);
        } catch (e) {
            console.log(e);
        }
    }
};