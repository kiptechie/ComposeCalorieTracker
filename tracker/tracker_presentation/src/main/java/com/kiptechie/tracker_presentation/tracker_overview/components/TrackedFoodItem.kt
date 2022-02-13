package com.kiptechie.tracker_presentation.tracker_overview.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.kiptechie.core.R
import com.kiptechie.core_ui.LocalSpacing
import com.kiptechie.tracker_domain.models.TrackedFood
import com.kiptechie.tracker_presentation.composables.NutrientInfo

@ExperimentalCoilApi
@Composable
fun TrackedFoodItem(
    trackedFood: TrackedFood,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dimens = LocalSpacing.current
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(5.dp))
            .padding(dimens.extraSmall)
            .shadow(
                elevation = 1.dp,
                shape = RoundedCornerShape(5.dp)
            )
            .background(MaterialTheme.colors.surface)
            .padding(end = dimens.medium)
            .height(100.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberImagePainter(
                data = trackedFood.imageUrl,
                builder = {
                    crossfade(true)
                    error(R.drawable.ic_burger)
                    fallback(R.drawable.ic_burger)
                }
            ),
            contentDescription = trackedFood.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(1f)
                .clip(
                    RoundedCornerShape(
                        topStart = 5.dp,
                        bottomStart = 5.dp
                    )
                )
        )
        Spacer(modifier = Modifier.width(dimens.medium))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = trackedFood.name,
                style = MaterialTheme.typography.body1,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(dimens.extraSmall))
            Text(
                text = stringResource(
                    id = R.string.nutrient_info,
                    trackedFood.amount,
                    trackedFood.calories
                ),
                style = MaterialTheme.typography.body2
            )
            Spacer(modifier = Modifier.width(dimens.medium))
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(id = R.string.delete),
                    modifier = Modifier
                        .align(Alignment.End)
                        .clickable { onDeleteClick() }
                )
                Spacer(modifier = Modifier.height(dimens.extraSmall))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NutrientInfo(
                        name = stringResource(id = R.string.carbs),
                        amount = trackedFood.carbs,
                        unit = stringResource(id = R.string.grams),
                        amountTextSize = 16.sp,
                        unitTextSize = 12.sp,
                        nameTextStyle = MaterialTheme.typography.body2
                    )
                    Spacer(modifier = Modifier.width(dimens.small))
                    NutrientInfo(
                        name = stringResource(id = R.string.protein),
                        amount = trackedFood.protein,
                        unit = stringResource(id = R.string.grams),
                        amountTextSize = 16.sp,
                        unitTextSize = 12.sp,
                        nameTextStyle = MaterialTheme.typography.body2
                    )
                    Spacer(modifier = Modifier.width(dimens.small))
                    NutrientInfo(
                        name = stringResource(id = R.string.fat),
                        amount = trackedFood.fat,
                        unit = stringResource(id = R.string.grams),
                        amountTextSize = 16.sp,
                        unitTextSize = 12.sp,
                        nameTextStyle = MaterialTheme.typography.body2
                    )
                }
            }
        }
    }
}