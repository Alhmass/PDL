// routerUtils.ts

import router from '@/router';
import { api } from '@/http-api';
import { ImageType } from '@/image'

export const utils = {
    gotoImage(imageID: number) {
        router.push({ name: 'image', params: { id: imageID } });
    },
    getImages(imageList: ImageType[]) {
        api.getImageList().then((data) => {
            imageList.splice(0, imageList.length, ...data);
        }).catch(e => {
            console.log(e.message);
        });
    }
};